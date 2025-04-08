# Create a temporary directory
$tempDir = "temp_fonts"
New-Item -ItemType Directory -Path $tempDir -Force

# Download Poppins Bold
Invoke-WebRequest -Uri "https://github.com/google/fonts/raw/main/ofl/poppins/Poppins-Bold.ttf" -OutFile "$tempDir\poppins_bold.ttf"

# Download Poppins SemiBold
Invoke-WebRequest -Uri "https://github.com/google/fonts/raw/main/ofl/poppins/Poppins-SemiBold.ttf" -OutFile "$tempDir\poppins_semibold.ttf"

# Copy files to the raw directory
Copy-Item "$tempDir\poppins_bold.ttf" -Destination "app\src\main\res\raw\poppins_bold_file.ttf"
Copy-Item "$tempDir\poppins_semibold.ttf" -Destination "app\src\main\res\raw\poppins_semibold_file.ttf"

# Clean up
Remove-Item -Path $tempDir -Recurse -Force

Write-Host "Font files downloaded and placed in the raw directory." 