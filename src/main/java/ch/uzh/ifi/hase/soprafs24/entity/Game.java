package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "GAME")
public class Game implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long gameId;

  @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
  private List<User> players = new ArrayList<>();
  
  @ElementCollection
  @CollectionTable(name = "scoreboard", joinColumns = @JoinColumn(name = "gameId"))
  @MapKeyColumn(name = "username")
  @Column(name = "userscore")
  private Map<String, Integer> scoreBoard = new HashMap<>();
  
  @Column(nullable = false)
  private int hintsNumber;

  @Column(nullable = false)
  private String gameCreationDate;


  public Long getGameId() {
    return gameId;
  }

  public void setGameId(Long gameId) {
    this.gameId = gameId;
  }

  //add player
  public void addPlayer(User player) {
    players.add(player);
    player.setGame(this); 
  }

  //remove player
  public void removePlayer(User player) {
      players.remove(player);
      player.setGame(null); 
  }

  //get scoreBoard
  public Map<String, Integer> getScoreBoard() {
      return scoreBoard;
  }

  public void setScoreBoard(Map<String, Integer> scoreBoard) {
      this.scoreBoard = scoreBoard;
  }

  // update scoreBoard
  public void updateScore(String username, int score) {
      scoreBoard.put(username, score);
  }

  // get specific user's score
  public Integer getScore(String username) {
      return scoreBoard.get(username);
  }

  // remove specific user's score
  public Integer removeScore(String username) {
      return scoreBoard.remove(username);
  }

  public int getHintsNumber() {
    return hintsNumber;
  }

  public void setHintsNumber(int hintsNumber) {
    this.hintsNumber = hintsNumber;
  }
  
  public String getGameCreationDate() {
    return gameCreationDate;
  }

  public void setGameCreationDate(String gameCreationDate) {
    this.gameCreationDate = gameCreationDate;
  }
}
