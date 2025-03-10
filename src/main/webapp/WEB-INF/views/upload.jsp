<!DOCTYPE html>
<html>
<head>
    <title>OCR PDF Merge</title>
</head>
<body>
    <h2>Select PDFs to Upload and Convert to OCR</h2>
    <form method="post" action="/upload" enctype="multipart/form-data">
        <input type="file" name="files" multiple required />
        <button type="submit">Upload and Convert</button>
    </form>
</body>
</html>
