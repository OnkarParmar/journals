package com.teamteach.journals.services;

import java.util.Date;
import java.util.HashMap;

import javax.inject.Singleton;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonString;

import com.teamteach.journals.domains.Journal;
import com.teamteach.journals.domains.enums.JournalStatus;
import io.reactivex.Flowable;
import io.reactivex.Single;


@Singleton
public class JournalService {

    private final MongoClient mongoClient;
    

    public JournalService(MongoClient mongoClient)
    {
        this.mongoClient = mongoClient;
    }


    private MongoCollection<Journal> getCollection() {
            return mongoClient
                .getDatabase("digisherpa")
                .getCollection("journals", Journal.class);
    }

    private Single<Journal> findAsSingle(BsonDocument query)
    {

        return Single.fromPublisher(getCollection().find(query)); 
    }
    
    private Flowable<Journal> findAsFlowable(BsonDocument query)
    {

        return Flowable.fromPublisher(getCollection().find(query)); 
    }

    public Single<Journal> save(Journal journal){

        Long x = Single.fromPublisher(getCollection()
                .countDocuments(new BsonDocument()
                        .append("journalerName", new BsonString(journal.getJournalerName()))))
                        .blockingGet();
        journal.setId(journal.getJournalerName() + "_" + x.longValue());
        journal.setStatus(JournalStatus.CREATED);

        return   Single.fromPublisher(getCollection().insertOne(journal))
                .map(success->journal);

    }
    
    public Single<Journal> findJournalByNo(String journalNo) {
        
        return findAsSingle(new BsonDocument().append("_id", new BsonString(journalNo))); 
        
    }
    
    public Flowable<Journal> findAll()
    {
            BsonDocument query = new BsonDocument().append("status", new BsonString(JournalStatus.CREATED.toString())); 
            return findAsFlowable(query); 

    }
    
    public Flowable<Journal> findAll(String username){
        return findAsFlowable(new BsonDocument()
                                .append("journalerName", new BsonString(username))); 
    }
    public Flowable<Journal> findByName(String name) {
        BsonDocument query = new BsonDocument()
        .append("status", new BsonString(JournalStatus.CREATED.toString()))
        .append("title", new BsonString(name)); 

        return findAsFlowable(query); 
    }

    public Single<String> takeAction(String journalId, JournalStatus status){
        BsonDocument filter = new BsonDocument().append("_id", new BsonString(journalId)); 

        Journal journal = findAsSingle(filter).blockingGet(); 
        //Single.fromPublisher(getCollection().find(filter).limit(1).first()).blockingGet(); 
        journal.setStatus(status);
        journal.setLastUpdate(new Date());

        return Single.fromPublisher(getCollection().findOneAndReplace(filter, journal))
        .map(success->"success")
        .onErrorReturnItem("failed"); 
    }
}
