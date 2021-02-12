<!DOCTYPE html>
<html>
    <head>
        <title>Hello Servlet: Home page</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
        <#if bannerMessage??>
        <p style="background-color:yellow">${bannerMessage}</p>
        </#if>
        <h1>Hello World from ${ownerName}'s Server!</h1>
        <h3>This page has been loaded ${n} times since the server was CREATED!</h3>
        <p><a href="servlet?cmd=session">Your own personal page!</a></p>
        <p><a href="servlet?cmd=about">About this website</a></p>
        <p>v${version}</p>
    </body>
</html>
