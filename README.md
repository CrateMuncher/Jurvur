# Setup
Feel free to edit anything in config.json and database.json.

# Database
The preferred way to give the bot database info is making an environment variable called "DATABASE_URL" with info in the following format:

    <dbtype>://<username>:<password>@<host>:<port>/<database>

Where <dbtype> is either "postgres", "sqlite" or "mysql". Remember to add the appropriate driver to pom.xml and config.json.