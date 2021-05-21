runs = new Mongo.Collection('runs');

/* runs.attachSchema(
    new SimpleSchema({
    title: {
      type: String
    },
    content: {
      type: String
    },
    createdAt: {
      type: Date,
      denyUpdate: true
    }
  })
);*/

// Collection2 already does schema checking
// Add custom permission rules if needed
if (Meteor.isServer) {
    runs.allow({
        insert () {
            return true;
        },
        update () {
            return true;
        },
        remove () {
            return true;
        },
    });
}

if (Meteor.isClient) {
    runs.allow({
        insert () {
            return true;
        },
        update () {
            return true;
        },
        remove () {
            return true;
        },
    });
}
