**Bot Specific Information**

HelpBot is a bot focused on providing a large amount of commands to the DiamondFire community. Please note, HelpBot is
only intended to be used on the DiamondFire discord only. Commands vary from stats related commands to commands that
provide codeblock information.

***

**Contribution**

HelpBot is created in **java 16** and there are no current rules in place for contribution. If you want to add/modify
something you are able to create a **fork** and submit a **pull request**.

Most pull requests are accepted, do not be afraid to make a change I promise I do not bite.
Please give me time to review your PR however, as it takes a little bit for me to make sure that your PR is up to standards. Testing will help!

**Config File**

This project uses a config file inorder to specify things such as bot token, prefix and database information. When you
first boot the bot, it will error and shut down the bot. In the working directory a config file should have been
created, use the following format.

```json
{
     "token": "bot token",
     "prefix": "?",
     "dev_bot": true,
     
     "mc_email": "Minecraft@email.com",
     "mc_password": "MinecraftPassword",
     
     "db_link": "jdbc:db_type://ip:port/schema",
     "db_user": "dbuser",
     "db_password": "dbpassword",
     
     "guild": long,
     "log_channel": long,
     "discussion_channel": long,
     "muted_role": long,
     "verified_role": long,
     
     "role_react_channel": long,
     "role_react_message": long
     
   }
```

**Swear Filter**

After setting up the config, you will need to paste the following into the swear filter file. Otherwise, commands will not work and it will error whenever a message is sent.

```json
{
  "equal": [
  ],
  "prefix": [
  ],
  "suffix": [
  ],
  "part": [
  ]
}
```
