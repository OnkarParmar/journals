package com.teamteach.journals;

import javax.inject.Inject;

import com.mongodb.reactivestreams.client.MongoClient;

import org.bson.BsonDocument;
import org.bson.BsonString;

import com.teamteach.journals.domains.Journal;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.Micronaut;
import io.micronaut.runtime.event.annotation.EventListener;

public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class);
    }

    @Inject
    private MongoClient mongoClient; 

    @EventListener
    void init(StartupEvent startupEvent)
    {
        //create 2dsphere index to query journals by locations. 
        mongoClient
        .getDatabase("digisherpa")
        .getCollection("journals", Journal.class)
        .createIndex(new BsonDocument()
        .append("location", new BsonString("2dsphere")));
    }
}
