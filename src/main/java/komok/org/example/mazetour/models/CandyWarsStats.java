package komok.org.example.mazetour.models;

public class CandyWarsStats {
    private String nickname;
    private int candies;
    private int deaths;
    private int kills;

    public CandyWarsStats(String nickname, int candies, int deaths, int kills) {
        this.nickname = nickname;
        this.candies = candies;
        this.deaths = deaths;
        this.kills = kills;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getCandies() {
        return candies;
    }

    public void setCandies(int candies) {
        this.candies = candies;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }
}
