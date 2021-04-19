package xyz.rebasing.rebot.service.persistence.api;

import javax.inject.Inject;
import javax.transaction.Transactional;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import xyz.rebasing.rebot.service.persistence.pojo.CommandStatus;
import xyz.rebasing.rebot.service.persistence.repository.ApiRepository;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommandTest {

    @Inject
    ApiRepository repository;

    @Test
    @Order(1)
    @Transactional
    public void testDisableCommand() {
        // first the command should be enabled as there is nothing in the table
        Assertions.assertTrue(repository.isCommandEnabled(-1, "command1"));
        Assertions.assertTrue(repository.isCommandEnabled(-1, "command2"));

        // disable both commands
        repository.disableCommand(new CommandStatus(-1L, "command1", false));
        repository.disableCommand(new CommandStatus(-1L, "command2", false));

        // expected to be disable
        Assertions.assertFalse(repository.isCommandEnabled(-1, "command1"));
        Assertions.assertFalse(repository.isCommandEnabled(-1, "command2"));
    }

    @Test
    @Order(2)
    public void testEnableCommand() {

        // enable boot commands
        repository.enableCommand(-1L, "command1");
        repository.enableCommand(-1L, "command2");

        // commands expected not to be enabled
        Assertions.assertTrue(repository.isCommandEnabled(-1, "command1"));
        Assertions.assertTrue(repository.isCommandEnabled(-1, "command2"));
    }
}
