package com.hackathon;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;

public class ReleaseTimelineAnalyzerApplication extends Application<Configuration> {

    @Override
    public void run(Configuration timelineConfiguration, Environment environment) throws Exception {
    }

    public static void main(String[] args) throws Exception {
        new ReleaseTimelineAnalyzerApplication().run(args);
    }
}