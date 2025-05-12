package edu.uob;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alexmerz.graphviz.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.io.IOException;
import java.time.Duration;

class ExampleSTAGTests {

  private GameServer server;

  // Create a new server _before_ every @Test
  @BeforeEach
  void setup() {
      File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
      File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
      server = new GameServer(entitiesFile, actionsFile);
  }

  String sendCommandToServer(String command) {
      // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
      return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
      "Server took too long to respond (probably stuck in an infinite loop)");
  }

  // A lot of tests will probably check the game state using 'look' - so we better make sure 'look' works well !
  @Test
  void testLook() {
    String response = sendCommandToServer("simon: look");
    response = response.toLowerCase();
    assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
    assertTrue(response.contains("log cabin"), "Did not see a description of the room in response to look");
    assertTrue(response.contains("magic potion"), "Did not see a description of artifacts in response to look");
    assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
    assertTrue(response.contains("forest"), "Did not see available paths in response to look");
  }

  // Test that we can pick something up and that it appears in our inventory
  @Test
  void testGet()
  {
      String response;
      sendCommandToServer("simon: get potion");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("potion"), "Did not see the potion in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("potion"), "Potion is still present in the room after an attempt was made to get it");
      sendCommandToServer("simon: drop potion");
      response = sendCommandToServer("simon: inv");
      assertFalse(response.contains("potion"));
      response = sendCommandToServer("simon: look");
      assertTrue(response.contains("potion"));
  }

  // Test that we can goto a different location (we won't get very far if we can't move around the game !)
  @Test
  void testGoto()
  {
      sendCommandToServer("simon: goto forest");
      String response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("key"), "Failed attempt to use 'goto' command to move to the forest - there is no key in the current location");
  }

  // Add more unit tests or integration tests here.
  @Test
  void testCustom()
  {
      String response = sendCommandToServer("simon: goto forest");
      response = sendCommandToServer("simon: get key");
      response = sendCommandToServer("simon: goto cabin");
      response = sendCommandToServer("simon: open the trapdoor");
      response = response.toLowerCase();
      response = sendCommandToServer("simon: look");
      sendCommandToServer("simon: get axe");
      sendCommandToServer("simon: goto forest");
      sendCommandToServer("simon: cut down tree please");
      response = sendCommandToServer("simon: look");
      sendCommandToServer("simon: goto cabin");
      response = sendCommandToServer("simon: drink a potion");
      response = sendCommandToServer("simon: look");
      sendCommandToServer("simon: goto cellar");
      response = sendCommandToServer("simon: fight with elf");
      sendCommandToServer("simon: fight with elf");
      response = sendCommandToServer("simon: fight with elf");
      sendCommandToServer("simon: get coin");
      sendCommandToServer("simon: goto cellar");
      sendCommandToServer("simon: pay elf with coin");
      sendCommandToServer("simon: get shovel");
      sendCommandToServer("simon: goto cabin");
      sendCommandToServer("simon: goto forest");
      response = sendCommandToServer("simon: look");
      sendCommandToServer("simon: goto riverbank");
      sendCommandToServer("simon: bridge the river");
      response = sendCommandToServer("simon: look");
      sendCommandToServer("simon: goto clearing");
      sendCommandToServer("simon: dig ground with shovel");
      response = sendCommandToServer("simon: look");
      sendCommandToServer("simon: goto riverbank");
      response = sendCommandToServer("simon: blow horn");
      response = sendCommandToServer("simon: look");
  }

    @Test
    void testTwoPlayers()
    {
        String response = sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: get key");
        response = sendCommandToServer("simon: goto cabin");
        response = sendCommandToServer("simon: open the trapdoor");
        response = response.toLowerCase();
        response = sendCommandToServer("simon: look");

        sendCommandToServer("peter: get axe");
        sendCommandToServer("peter: goto forest");
        sendCommandToServer("peter: cut down tree please");
        response = sendCommandToServer("peter: look");

        sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon: fight with elf");
        sendCommandToServer("simon: fight with elf");
        response = sendCommandToServer("simon: fight with elf");

        sendCommandToServer("peter: goto cabin");
        response = sendCommandToServer("peter: drink a potion");
        response = sendCommandToServer("peter: look");
    }
}
