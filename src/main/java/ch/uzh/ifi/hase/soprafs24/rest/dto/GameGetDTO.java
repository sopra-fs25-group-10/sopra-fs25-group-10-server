package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.entity.User;

import java.util.List;
import java.util.Map;

public class GameGetDTO {

  private Long gameId;
  private List<User> players;
  private Map<String, String> scoreBoard;

  public Long getGameId() {
    return gameId;
  }

  public void setGameId(Long gameId) {
    this.gameId = gameId;
  }

  public List<User> getPlayers() {
    return players;
  }

  public void setPlayers(User player) {
    this.players.add(player);
  }

  // public String getUsername() {
  //   return username;
  // }

  // public void setUsername(String username) {
  //   this.username = username;
  // }

  }
