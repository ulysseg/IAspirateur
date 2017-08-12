package me.ulysse.iaspirateur.ia;

/**
 * Created by ulysse on 8/11/17.
 *
 * Can be used as a robot by {@link me.ulysse.iaspirateur.gui.RobotEngine}.
 */
public interface Robot {

    double calculateDirection(double leftBeamDistance, double centerBeamDistance, double rightBeamDistance);
}
