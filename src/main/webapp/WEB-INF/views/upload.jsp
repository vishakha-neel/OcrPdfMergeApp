


<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>OCR PDF Processor</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <script>
        function submitForm() {
            let fileInput = document.getElementById('pdfFiles');
            if (fileInput.files.length === 0) {
                alert("Please select at least one PDF file.");
                return;
            }

            let formData = new FormData();
            for (let i = 0; i < fileInput.files.length; i++) {
                formData.append("pdfFiles", fileInput.files[i]);
            }

            document.getElementById('processBtn').disabled = true;
            document.getElementById('processBtn').innerHTML = 'Processing...';

            fetch('/process', {
                method: 'POST',
                body: formData
            })
            .then(response => response.text())
            .then(data => {
                alert(data);
                document.getElementById('processBtn').disabled = false;
                document.getElementById('processBtn').innerHTML = 'Process PDFs';
            })
            .catch(error => {
                alert("Error processing OCR: " + error);
                document.getElementById('processBtn').disabled = false;
                document.getElementById('processBtn').innerHTML = 'Process PDFs';
            });
        }
    </script>
    <style>
        body {
            background-color: #f8f9fa;
        }
        .container {
            max-width: 500px;
            margin-top: 50px;
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
        }
    </style>
</head>
<body>
    <div class="container text-center">
        <h2 class="mb-4">OCR PDF Processor</h2>
        <input type="file" id="pdfFiles" class="form-control mb-3" multiple accept="application/pdf" />
        <button id="processBtn" class="btn btn-primary w-100" onclick="submitForm()">Process PDFs</button>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
