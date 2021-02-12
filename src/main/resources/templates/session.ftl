<!DOCTYPE html>
<html>
    <head>
        <title>Hello Servlet: Session page</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
        <#if bannerMessage??>
        <p style="background-color:yellow">${bannerMessage}</p>
        </#if>
        <h1>Hello World from ${ownerName}'s Server!</h1>
        <h3>This is your own personal page!  It has been loaded ${n} times since you created it.</h3>
        <p><a href="servlet?cmd=home">Home</a></p>
        <p><a href="servlet?cmd=about">About this website</a></p>
        <p>v${version}</p>
    </body>
</html>
