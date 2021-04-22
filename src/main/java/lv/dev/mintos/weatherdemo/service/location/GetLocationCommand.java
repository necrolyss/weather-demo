package lv.dev.mintos.weatherdemo.service.location;

import an.awesome.pipelinr.Command;
import lv.dev.mintos.weatherdemo.model.Location;

public record GetLocationCommand(String userIp) implements Command<Location> {

}

