const colors = ['#F33B52', '#EF7928', '#C8C02E', '#10A45B'];
function getColor(val) {


    if (val <= 25) {
        return colors[0]
    }
    if (val <= 50) {
        return colors[1]
    }
    if (val <= 75) {
        return colors[2]
    }
    if (val <= 100) {
        return colors[3]
    }

    return colors[0];
}

Template.sliderSummary.helpers({
    myColor() {
        return getColor(this.value);
    },
});

Template.sliderSummary.events({
});

Template.sliderSummary.rendered = function() {

};
