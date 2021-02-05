package lesson7.server.chat.auth;

import lesson7.server.chat.User;

import java.util.List;

public class BaseAuthService implements AuthService {


    private static final List<User> clients = List.of(
        new User("martin", "1111", "Мартин_Некотов"),
        new User("boris", "2222", "Борис_Николаевич"),
        new User("gena", "3333", "Гендальф_Серый")
    );

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for (User client : clients) {
            if(client.getLogin().equals(login) & client.getPassword().equals(password)) {
                return client.getUsername();
            }
        }
        return null;
    }

    @Override
    public void startAuthentication() {
        System.out.println("Старт аутентификации");
    }

    @Override
    public void endAuthentication() {
        System.out.println("Окончание аутентификации");

    }
}
