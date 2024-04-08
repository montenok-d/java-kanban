import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import Model.Subtask;
import Model.Task;
import Model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{

    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager();
        super.beforeEach();
        //historyManager = new InMemoryHistoryManager();
    }
}
