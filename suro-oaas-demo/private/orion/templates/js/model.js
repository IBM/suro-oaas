// { "path" : "shared/models/__modelName__.js" }
__modelName__ = new Mongo.Collection('__modelName__');

__modelName__.attachSchema(
    new SimpleSchema({
        title: {
            type: String,
        },
        content: {
            type: String,
        },
        createdAt: {
            type: Date,
            denyUpdate: true,
        },
    })
);

// Collection2 already does schema checking
// Add custom permission rules if needed
if (Meteor.isServer) {
    __modelName__.allow({
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
