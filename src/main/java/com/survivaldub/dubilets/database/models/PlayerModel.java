package com.survivaldub.dubilets.database.models;

import java.util.UUID;

public class PlayerModel {

    private UUID uuid;
    private String nickname;
    private String language;
    private Long discordId;
    private Double coins;
    private Integer dubets;
    private Boolean isStaff;
    private Boolean isDgi;

    public PlayerModel(UUID uuid, String nickname, String language, Long discordId, Double coins, Integer dubets, Boolean isStaff, Boolean isDgi) {
        this.uuid = uuid;
        this.nickname = nickname;
        this.language = language;
        this.discordId = discordId;
        this.coins = coins;
        this.dubets = dubets;
        this.isStaff = isStaff;
        this.isDgi = isDgi;
    }

    public UUID getUuid() { return this.uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }
    public String getNickname() { return this.nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getLanguage() { return this.language; }
    public void setLanguage(String language) { this.language = language; }
    public Long getDiscordId() { return this.discordId; }
    public void setDiscordId(Long discordId) { this.discordId = discordId; }
    public Double getCoins() { return this.coins; }
    public void setCoins(Double coins) { this.coins = coins; }
    public Integer getDubets() { return this.dubets; }
    public void setDubets(Integer dubets) { this.dubets = dubets; }
    public Boolean getStaff() { return this.isStaff; }
    public void setStaff(Boolean staff) { this.isStaff = staff; }
    public Boolean getDgi() { return this.isDgi; }
    public void setDgi(Boolean dgi) { this.isDgi = dgi; }
}
