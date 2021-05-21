strategies = new Mongo.Collection('strategies');
/*
strategies.attachSchema(
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
    strategies.allow({
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
    strategies.allow({
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
