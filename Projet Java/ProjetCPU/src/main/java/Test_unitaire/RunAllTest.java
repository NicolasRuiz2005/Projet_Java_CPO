package Test_unitaire;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    MemoireTest.class,
    RegistreTest.class,
    ALUTest.class,
    CPUTest.class,
    InstructionTest.class,
    ProgrammeTest.class,
    AssembleurTest.class,
    SimulateurTest.class
})
public class RunAllTest {
}
