import io.qameta.allure.Step;

import java.util.Random;

public class User {
    private String email;
    private String password;
    private String name;

    public User(String email, String passeord, String name) {
        this.email = email;
        this.password = passeord;
        this.name = name;
    }
    @Step("Generate new user with random data")
    public void generateRandomUser(){  // In case of recent test fails switch to the next generation "gen0@mail.ru"
        Random random= new Random();
        this.email = (random.nextInt(10000))+"gen0@mail.ru";
        this.password= "testPassword1";
        this.name = "Test Name";
        // In case of recent test fails switch to the next generation "gen0@mail.ru", to guarantee unic test data
    }
    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPasseord(String passeord) {
        this.password = passeord;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
