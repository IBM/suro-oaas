var express = require('express')
var bodyParser = require('body-parser')
var app = express()

// initialise issue Id with 1 (will be incremented)
var issueId = 1;

// create parser
var jsonParser = bodyParser.json()

// declare functions to check if values are defined and/or have the right format
function isDefined(obj) {
  return (obj !== undefined && obj.trim() != "");
}
function isNumber(obj) {
  return (obj !== undefined && !isNaN(obj));
}


// POST /api/users gets JSON bodies
app.post('/issues.json', jsonParser, function (req, res) {
  if (!req.body)
    return res.status(400).end();

  if (req.body === undefined || req.body.issue === undefined) {
    return res.status(400).end();
  }
  var issue = req.body.issue;

  // validate issue request
  if (isDefined(issue.subject) && isDefined(issue.description) && isNumber(issue.project_id) && isNumber(issue.tracker_id) && isNumber(issue.priority_id) && isNumber(issue.status_id)) {

    // determine current date for creation date
    var dateNow = new Date();

    // set request headers (content type and return code)
    res.setHeader('content-type', 'application/json');
    res.writeHead(201)

    // write API response
    res.end(JSON.stringify({
      "issue": {
        "id": issueId,
        "project": {
          "id": issue.project_id,
          "name": "MockProject"
        },
        "tracker": {
          "id": issue.tracker_id,
          "name": "MockTracker"
        },
        "status": {
          "id": issue.status_id,
          "name": "MockStatus"
        },
        "priority": {
          "id": issue.priority_id,
          "name": "MockPriority"
        },
        "author": {
          "id": 1337,
          "name": "Justin Sane"
        },
        "subject": issue.subject,
        "description": issue.description,
        "start_date": dateNow.getFullYear() + "-" + dateNow.getMonth() + "-" + dateNow.getDate(),
        "done_ratio": 0,
        "custom_fields": [],
        "created_on": dateNow,
        "updated_on": dateNow
      }
    }));

    // increment the issue Id for the next issue
    issueId++;
    return res;
  }
  else {
    // validation failed, return 400 (invalid)
    return res.status(400)
              .end();
  }
});
// listen to port 3001
app.listen(3000);