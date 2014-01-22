# Setup
Feel free to edit anything in `config.json` and `database.json`.

You can override anything in the `config.json` or `database.json` using environment variables, which are interpreted as JSON.
For example, if you want to turn off GIF URL shortening without using the config file, simply set `JURVUR_SHORTEN_GIF_URL` to `false`.
Similarily, if you want to change the username, set `JURVUR_USERNAME` to `Thomas`, or whatever you want it to be. Note how you have to INCLUDE the `s`, because it's JSON.
It's JSON, so you can also set the channels, by setting `JURVUR_CHANNELS` to `["#channel1", "channel2"]`.
If you want to set a thing in `database.json`, simply use the prefix `JURVUR_DATABASE_` instead of `JURVUR_`.

# Database
The preferred way to give the bot database info is making an environment variable called `DATABASE_URL` with info in the following format:

    <dbtype>://<username>:<password>@<host>:<port>/<database>

Where `<dbtype>` is either `postgres`, `sqlite` or `mysql`. Remember to add the appropriate driver to `config.json`, or just set it with the `JURVUR_DATABASE_DRIVER`. Again, remember to put what you need to set in quotes (so the actual content of the environment variable actually has quotes)
The appropriate drivers are:
    `org.postgresql.Driver`
    `com.mysql.jdbc.Driver`
    `org.sqlite.JDBC`

# Running on Heroku
Running Jurvur on Heroku is very simple. It already includes a Procfile that you need, and automatically grabs the database needed.
You should already know the basics of using Heroku, I won't go into detail on how to set a project up.
