package lv.dev.mintos.weatherdemo.service.location;

import lv.dev.mintos.weatherdemo.infra.RestException;

class UnknownLocation extends RestException {

    public UnknownLocation(String userIp) {
        super("exception.unknown-location", userIp);
    }

}
