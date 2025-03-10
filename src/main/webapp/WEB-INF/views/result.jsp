<!DOCTYPE html>
<html>
<head>
    <title>OCR Result</title>
</head>
<body>
    <h2>${message}</h2>
    <c:if test="${not empty filePath}">
        <p>Download merged PDF: <a href="${filePath}" download>Download PDF</a></p>
    </c:if>
    <br />
    <a href="/">Back to Upload</a>
</body>
</html>
