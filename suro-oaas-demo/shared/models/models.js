models = new Mongo.Collection('models');

/* models.attachSchema(
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
    models.allow({
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


// Collection2 already does schema checking
// Add custom permission rules if needed
if (Meteor.isClient) {
    models.allow({
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
