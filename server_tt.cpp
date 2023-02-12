#ifdef _WIN32
#include <winsock2.h>
#else
#include <sys/select.h>
#include <unistd.h>
#include <arpa/inet.h>
#endif
#include <set>
#include <map>
enum struct conn_status
{
    PendingPassword,
    PendingChannel,
    PendingType,
    Connected,
};
struct connection
{
    int fd;
    conn_status status;
    int channel;
    bool client;
};
std::map<int, connection> fds;
std::map<int,std::set<int>> pmr_fds;
std::map<int,int> pcr_fds;
int main()
{
#ifdef _WIN32
    WSADATA wsa;
    WSAStartup(MAKEWORD(2,2), &wsa);
#endif
    int server = socket(AF_INET, SOCK_STREAM, 0);
    struct sockaddr_in serv;
    serv.sin_family = AF_INET;
    serv.sin_port = htons(48301);
    serv.sin_addr.s_addr = INADDR_ANY;
    if (server == -1) {
        //perror("socket");
        return 1;
    }
#ifdef _WIN32
    char ra = 1;
#else
    int ra = 1;
#endif
    if(setsockopt(server, SOL_SOCKET, SO_REUSEADDR, &ra, sizeof(ra))==-1) {
        //perror("setsockopt");
        return 1;
    }
    if (bind(server, (struct sockaddr *)&(serv), sizeof(serv)) == -1) {
        //perror("bind");
        return 1;
    }
    if (listen(server, SOMAXCONN) == -1) {
        //perror("listen");
        return 1;
    }
    while(true)
    {
        fd_set read_fds;
        fd_set error_fds;
        FD_ZERO(&read_fds);
        FD_ZERO(&error_fds);
        int nfds = server;
        for (auto it=fds.begin(); it!=fds.end(); ++it)
        {
            FD_SET(it->first, &read_fds);
            FD_SET(it->first, &error_fds);
            if (nfds < it->first) nfds = it->first;
        }
        FD_SET(server, &read_fds);
        std::set<int> old_fds;
        if (select(nfds+1, &read_fds, nullptr, &error_fds, nullptr) > 0)
        {
            if (FD_ISSET(server, &read_fds))
            {
                struct sockaddr_in addr;
                int c = sizeof(struct sockaddr_in);
                int cl = accept(server, (struct sockaddr *)&addr, 
#ifdef _WIN32
                    (int *)
#else
                    (socklen_t *)
#endif
                    &c);
                if(cl == -1)
                {
                    //perror("accept");
                }
                else
                {
                    connection con;
                    con.fd = cl;
                    con.status = conn_status::PendingChannel;
                    fds[cl] = con;
                }
            }
            for (auto it=fds.begin(); it!=fds.end(); ++it)
            {
                if (FD_ISSET(it->first, &error_fds))
                {
                    pmr_fds[it->second.channel].erase(it->first);
                    if (pcr_fds[it->second.channel] == it->first) pcr_fds.erase(it->second.channel);
                    old_fds.insert(it->first);
                }
                else if (FD_ISSET(it->first, &read_fds))
                {
                    char buff[8192];
                    int nread = recv(it->first, buff, sizeof(buff), 0);
                    if (nread <= 0)
                    {
                        pmr_fds[it->second.channel].erase(it->first);
                        if (pcr_fds[it->second.channel] == it->first) pcr_fds.erase(it->second.channel);
                        old_fds.insert(it->first);
                    }
                    else
                    {
                        for (int i=0; i<nread; )
                        {
                            switch (it->second.status)
                            {
                                case conn_status::PendingChannel:
                                    it->second.channel = buff[i];
                                    it->second.status = conn_status::PendingType;
                                    i++;
                                    break;
                                case conn_status::PendingType:
                                    it->second.client = buff[i];
                                    it->second.status = conn_status::Connected;
                                    if (it->second.client)
                                    {
                                        pmr_fds[it->second.channel].insert(it->first);
                                    }
                                    else
                                    {
                                        auto it2 = pcr_fds.find(it->second.channel);
                                        if (it2 != pcr_fds.end()) old_fds.insert(it2->first);
                                        pcr_fds[it->second.channel] = it->first;
                                    }
                                    i++;
                                    break;
                                case conn_status::Connected:
                                    if (it->second.client)
                                    {
                                        auto it2 = pcr_fds.find(it->second.channel);
                                        if (it2 != pcr_fds.end() && send(it2->second, buff, nread-i, 0) == -1)
                                        {
                                            old_fds.insert(it2->second);
                                            pcr_fds.erase(it2);
                                        }
                                    }
                                    else
                                    {
                                        auto &pmrs = pmr_fds[it->second.channel];
                                        for (auto it2 = pmrs.begin(); it2 != pmrs.end(); )
                                        {
                                            if (send(*it2, buff, nread-i, 0) == -1)
                                            {
                                                old_fds.insert(*it2);
                                                it2 = pmrs.erase(it2);
                                            }
                                            else ++it2;
                                        }
                                    }
                                    i = nread;
                                    break;
                                default:
                                    i = nread;
                                    break;
                            }
                        }
                    }
                }
            }
            for (int fd : old_fds)
            {
                fds.erase(fd);
            }
        }
    }
}
