<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>OCR PDF Processor</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
	rel="stylesheet">
<script>
        // Function to process OCR PDFs
        function submitForm() {
            let fileInput = document.getElementById('ocrPdfFiles');
            let ocrDestinationPath = document.getElementById('ocrDestinationPath');
            
            if (fileInput.files.length === 0) {
                alert("Please select at least one PDF file.");
                return;
            }

            let formData = new FormData();
            for (let i = 0; i < fileInput.files.length; i++) {
                formData.append("pdfFiles", fileInput.files[i]);
            }
            
            formData.append("destinationpath", ocrDestinationPath.value);
            
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

        // Function to merge PDFs with destination path
        function submitForm1() 
        {
        	console.log('Called')
            let fileInput = document.getElementById('mergePdfFiles');
            let destinationInput = document.getElementById('destinationPath');

            if (fileInput.files.length === 0) {
                alert("Please select at least one PDF file.");
                return;
            }

            if (!destinationInput.value) {
                alert("Please specify the destination location.");
                return;
            }

            let formData = new FormData();
            
            // Add files to FormData
            for (let i = 0; i < fileInput.files.length; i++) {
                formData.append("files", fileInput.files[i]);
            }

            // Add destination path to FormData
            formData.append("destinationpath", destinationInput.value);

            document.getElementById('mergeBtn').disabled = true;
            document.getElementById('mergeBtn').innerHTML = 'Merging...';

            fetch('/merge', {
                method: 'POST',
                body: formData
            })
            .then(response => response.text())
            .then(data => {
                alert(data);
                document.getElementById('mergeBtn').disabled = false;
                document.getElementById('mergeBtn').innerHTML = 'Merge PDFs';
            })
            .catch(error => {
                alert("Error merging the PDFs: " + error);
                document.getElementById('mergeBtn').disabled = false;
                document.getElementById('mergeBtn').innerHTML = 'Merge PDFs';
            });
        }

        // Function to Rename PDFs with destination path
        function submitForm2() 
        {
        	console.log('Called')
            let fileInput = document.getElementById('renamePdfFiles');
            let destinationInput = document.getElementById('renameDestinationPath');

            if (fileInput.files.length === 0) {
                alert("Please select at least one PDF file.");
                return;
            }

            if (!destinationInput.value) {
                alert("Please specify the destination location.");
                return;
            }

            let formData = new FormData();
            
            // Add files to FormData
            for (let i = 0; i < fileInput.files.length; i++) {
                formData.append("files", fileInput.files[i]);
            }

            // Add destination path to FormData
            formData.append("renameDestinationpath", destinationInput.value);

            document.getElementById('renameBtn').disabled = true;
            document.getElementById('renameBtn').innerHTML = 'Renaming...';

            fetch('/rename', {
                method: 'POST',
                body: formData
            })
            .then(response => response.text())
            .then(data => {
                alert(data);
                document.getElementById('renameBtn').disabled = false;
                document.getElementById('renameBtn').innerHTML = 'Rename PDFs';
            })
            .catch(error => {
                alert("Error merging the PDFs: " + error);
                document.getElementById('renameBtn').disabled = false;
                document.getElementById('renameBtn').innerHTML = 'Rename PDFs';
            });
        }
    </script>

<style>
body {
	background-color: #f8f9fa;
}

.container {
	max-width: 600px;
	margin: 30px auto;
	background: white;
	padding: 30px;
	border-radius: 10px;
	box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
}

label {
	font-weight: bold;
}
</style>
</head>

<body>

	<!-- OCR PDF Processor -->
	
	<h2 class="text-center mt-5 mb-5 text-danger">Ocr, Merge Rename My Pdf Software.</h2>

	<div class="row">
		<div class="col-6 container text-center">
			<h2 class="mb-4">OCR PDF</h2>

			<label for="ocrDestinationPath" class="form-label">Destination
				Location:</label> <input type="text" id="ocrDestinationPath"
				class="form-control mb-3" placeholder="e.g., D://Input" /> <label
				for="ocrPdfFiles" class="form-label">Select PDF Files For
				Ocr Conversion:</label> <input type="file" id="ocrPdfFiles"
				class="form-control mb-3" multiple accept="application/pdf" />
			<button id="processBtn" class="btn btn-primary w-100"
				onclick="submitForm()">Process PDFs</button>
		</div>

		<!-- PDF Merger with Destination Path -->
		<div class="col-6 container text-center">
			<h2 class="mb-4">PDF Merger</h2>

			<label for="destinationPath" class="form-label">Destination
				Location:</label> <input type="text" id="destinationPath"
				class="form-control mb-3" placeholder="e.g., D://Input" /> <label
				for="mergePdfFiles" class="form-label">Select PDF Files
				Merge:</label> <input type="file" id="mergePdfFiles"
				class="form-control mb-3" multiple accept="application/pdf" />

			<button id="mergeBtn" class="btn btn-success w-100"
				onclick="submitForm1()">Merge PDFs</button>
		</div>

	</div>
    <div class="row">
        <div class="col-6 container text-center">
			<h2 class="mb-4">Rename PDF</h2>

			<label for="renameDestinationPath" class="form-label">Destination
				Location:</label> <input type="text" id="renameDestinationPath"
				class="form-control mb-3" placeholder="e.g., D://Output" /> <label
				for="renamePdfFiles" class="form-label">Select PDF Files For
				Rename </label> <input type="file" id="renamePdfFiles"
				class="form-control mb-3" multiple accept="application/pdf" />
			<button id="renameBtn" class="btn btn-primary w-100"
				onclick="submitForm2()">Rename PDFs</button>
		</div>
    </div>
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
