<!DOCTYPE html>
<html>
    <head>
        <title>Hello Servlet: About page</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
        <h1>About ${ownerName}'s Server!</h1>
        <h3>This website was created by ${ownerName} using the following technologies:</h3>
        <p>
            <ul>
                <li>Java</li>
                <li>Apache Maven</li>
                <li>Apache Tomcat</li>
                <li>Apache Freemarker</li>
                <li>VS Code</li>
                <li>Log4j2</li>
                <li>The Java HTTP Servlet Spec</li>
                <li>Amazon Web Services</li>
                <li>Ubuntu Linux</li>
                <li>Jenkins CI</li>
                <li>HTML</li>
                <li>HTTP</li>
                <li>Canvas</li>
                <li>CodeGrade</li>
                <li>Chocolatey and/or Homebrew</li>
                <li>...and finally, the good folks at Zoom!</li>
            </ul>
        </p>
        <p><a href="servlet?cmd=home">Home</a></p>
        <p><a href="servlet?cmd=session">Your own personal page!</a></p>
        <p>v${version}</p>
    </body>
</html>
