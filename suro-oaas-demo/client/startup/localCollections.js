/**
 * This local collection is a client side only collection, it will be used to store the current runs.
 * Because each run may be around one megabyte in size, we will use these as references.
 * @type {Mongo.Collection}
 */


// Runs=new Mongo.Collection(null);

/* var runs=[
    {
        runTitle:'Older',
        date_created:new Date("July 04, 2015 01:15:00"),
        id:0
    },{
        runTitle:'Newer',
        date_created:new Date("July 5, 2016 01:15:00"),
        id:1
    }
]*/


CachedRuns = new Mongo.Collection(null);
