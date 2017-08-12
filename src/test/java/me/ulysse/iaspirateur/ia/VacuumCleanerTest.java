package me.ulysse.iaspirateur.ia;

import org.junit.Test;

import static org.junit.Assert.*;

public class VacuumCleanerTest {
    @Test
    public void evaluateFitness() throws Exception {
        VacuumCleaner robot = VacuumCleaner.random();
        robot.evaluateFitness();
    }

}