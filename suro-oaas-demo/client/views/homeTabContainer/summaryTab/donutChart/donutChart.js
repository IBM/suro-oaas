const parseBedsByDepartment = function () {
    const beds = [];
    const tbeds = Template.donutChart.solutionHandler.bedsByDepartment;


    jQuery.each(tbeds, (i, val) => {
        const tbed = {
            label: i,
            count: val,

        };
        beds.push(tbed);
    });
    return beds;
}

let pieChart = null;

renderDonut = function () {

    const solution = currentSolution.get();
    const dataset = parseBedsByDepartment();

    const width = $('.donutDiv').width();
    const height = 260;
    const radius = 90;
    const donutWidth = 65;
    const legendRectSize = 18;
    const legendSpacing = 4;

    const color = d3.scale.ordinal()
        .range(['#517AEE', '#8EAAF6', '#1DC7D9', '#306F1C', '#5B984D', '#F9B474',
            '#FEFD6E', '#FBDB72', '#FA9B83', '#EF724E', '#E1451A']);


    const svg = d3.select('.donutDiv')
        .append('svg')
        .attr('width', width)
        .attr('height', height)
        .append('g')
        .attr('transform', `translate(140,${height / 2})`);

    pieChart = svg;

    const arc = d3.svg.arc()
        .innerRadius(radius - donutWidth)
        .outerRadius(radius);

    const pie = d3.layout.pie()
        .value((d) => d.count)
        .sort(null);

    const path = svg.selectAll('path')
        .data(pie(dataset))
        .enter()
        .append('path')
        .attr('d', arc)
        .attr('fill', (d, i) => color(d.data.label))
        .attr('transform', 'translate(0,30)');


    /* LEGEND */
    const legend = svg.selectAll('.legend')
        .data(color.domain())
        .enter()
        .append('g')
        .attr('class', 'legend')
        .attr('transform', (d, i) => {
            const height2 = legendRectSize + legendSpacing;
            const offset = (height2 * color.domain().length) / 2;
            const horz = -2 * legendRectSize;
            const vert = (i * height2) - offset;
            return `translate(180,${vert})`;
        });

    legend.append('rect')
        .attr('width', legendRectSize)
        .attr('height', legendRectSize)
        .style('fill', color)
        .style('stroke', color);

    legend.append('text')
        .attr('x', legendRectSize + legendSpacing)
        .attr('y', legendRectSize - legendSpacing)
        .text((d) => d);

}

Template.donutChart.currentChartData = new ReactiveVar([]);


Template.donutChart.helpers({
    renderDonutChart() {

    },
    solutionAvailable() {

        if (currentSolution.get() !== undefined) {
            // the moment the solution gets set, create the handler and parse the solution
            Template.donutChart.solutionHandler = new BedSolutionHandler(currentSolution.get());
            Template.donutChart.solutionHandler.parse();
            return true;
        }

        return false;

    },
});

Template.donutChart.events({});

Template.donutChart.rendered = function () {
    Tracker.autorun(() => {
        try {
            if (pieChart != null) {
                pieChart.remove();
            }
            $('.donutDiv').empty();

        } catch (err) {
            console.log(err);
        }
        const solution = currentSolution.get();
        renderDonut()
    });
};
