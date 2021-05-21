# Content of the folder

This folder contains the trust stores that have been used to test the secure connection to Redmine Installations (or mocks of those).

There are two trust stores:

1. __ibm-trust-store__: this is a custom trust store that contains only the IBM Internal CA root certficate and it is used to validate the connection to redmine. The password is: `r3dm1n3` 
2. __cacerts__: default Java trust store, used to verify all the certificates that are signed by the most known CAs. The password is `changeit`.

The two certificates can be used to simulate the different conditions that will enable us to test the various options available with RedmineIssueManager component.


