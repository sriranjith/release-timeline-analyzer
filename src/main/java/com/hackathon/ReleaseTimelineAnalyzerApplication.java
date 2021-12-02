package com.hackathon;

import com.hackathon.resource.ReleaseResource;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;

import java.util.ArrayList;

public class ReleaseTimelineAnalyzerApplication extends Application<Configuration> {

    @Override
    public void run(Configuration timelineConfiguration, Environment environment) throws Exception {
        ReleaseResource releaseResource = new ReleaseResource();
        environment.jersey().register(releaseResource);
    }


    public static void main(String[] args) throws Exception {
        new ReleaseTimelineAnalyzerApplication().run(args);
        /*ReleaseResource releaseResource = new ReleaseResource();
        releaseResource.getTicketDetails("1");
        System.out.println(releaseResource.getRelease(new ArrayList<>(), "1"));*/
    }
}