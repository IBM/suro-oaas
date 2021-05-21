

# Tool List

## Cloudant Data Export

Java program to export the content (documents including design documents) from a cloudant database instance and store
them in the file system (one file per document). For **design documents**, the `_id` and `_rev` of the document is removed
before dumping the content into a file.

Export files are stored in a sub-directory `./data/<timestamp>-cloudant-data`

**Usage:**

* Edit `com.ibm.au.optim.suro.tools.cloudant.ExportData` to specify the `CLOUDANT_ROOT_URL`, which contains
the endpoint (host/port), the credentials and the database name.
* Run `com.ibm.au.optim.suro.tools.cloudant.ExportData` without any parameters

**Reference:**

* The data produced by this tool can be used by `suro_oaas_couch/scripts/couch-import.sh` to import the cloudant data into a
local CouchDB instance.
