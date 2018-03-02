# tube-data-loader
Test Data Generators hosted as a RESTful service

**Note** this behaves like how Slack would when we request data from it - by default, this project uses _pagination_.

# Usage

Like any `sbt`-based project, what you normally do is to download OpenJDK 8+ (or beyond) with `Sbt`. You can run it directly by from the `sbt run` console which eventually would listen to all requests on Ipv4 interface on post `8080`.

## Example

When its started, the data is already seeded but the state is not. What you need to do is to make a first call (like how you would trigger Slack's APIs):
`curl localhost:8080/users?limit=<some number>&token=<some token>` 

This would return you json data directly (with pagination built-in) and to get the next batch of data you do something like this: `curl localhost:8080/users?limit=<some number>&token=<some token>&cursor=1` 

The above command would return you the next _block_ of data.

**Note:** We don't support dynamically changing of block sizes once the data is seeded so the first call is important. You need to decide how big the block sizes need to be i.e. `limit=<some number>` 

# Notes

You can change the configuration file (i.e. `application.conf`), in HOCON format, to alter how many:
* users to generate
* team emojis to generate
* channels to generate
* time range to generate

Just beaware that data is generated eagerly and the memory footprint can be large. Adjust the heap sizes and/or bigger capacity machine to host more test data.

