package com.caselife.logic.world.pathfinding;

import com.caselife.logic.world.Node;

import java.util.ArrayList;

public interface IPathfinder {

    public Path getPath(double[][] valueMap, Node origin, Node target);

    public void registerPath(Path path);

    public void unRegisterPath(Path path);

    public ArrayList<Path> getRegisteredPaths();

}
