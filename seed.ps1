# ==========================================================
#  Step 0 - seed data, entered through YOUR OWN endpoints
#  Run ONCE on an empty database, with the API already running:
#     powershell -ExecutionPolicy Bypass -File .\seed.ps1
#  It also writes edutech.postman_environment.json (import it in Postman).
# ==========================================================
$ErrorActionPreference = "Stop"
$base = "http://localhost:8080/api"

function Send($method, $path, $body) {
    if ($body) {
        $json = ConvertTo-Json -InputObject $body -Depth 6
        return Invoke-RestMethod -Method $method -Uri "$base/$path" -ContentType "application/json" -Body $json
    }
    return Invoke-RestMethod -Method $method -Uri "$base/$path"
}

# ---------- Subjects (4) - nobody will hold Computer Science ----------
foreach ($n in "Mathematics", "Physics", "English", "Computer Science") {
    Send Post "subjects/save" @{ name = $n } | Out-Null
}
$subjects = @(Send Get "subjects/all")
function SubjectId($name) { ($subjects | Where-Object { $_.name -eq $name }).id }

# ---------- Studios (4) - studio 4 will have no tutor ----------
foreach ($i in 1..4) {
    Send Post "studios/save" @{ studioNumber = $i; name = "Studio $i" } | Out-Null
}
$studios = @(Send Get "studios/all")
function StudioId($number) { ($studios | Where-Object { $_.studioNumber -eq $number }).id }

# ---------- Tutors (3) - each with a studio; one holds 2 subjects, others hold 1 ----------
$tutorData = @(
    @{ firstName = "Alice"; lastName = "Mukamana"; email = "alice@edutech.rw"; studio = 1; subjects = @("Mathematics", "Physics") },
    @{ firstName = "Jean";  lastName = "Niyonzima"; email = "jean@edutech.rw";  studio = 2; subjects = @("English") },
    @{ firstName = "Grace"; lastName = "Uwase";     email = "grace@edutech.rw"; studio = 3; subjects = @("Physics") }
)
foreach ($t in $tutorData) {
    $subjectRefs = @()
    foreach ($s in $t.subjects) { $subjectRefs += @{ id = (SubjectId $s) } }
    Send Post "tutors/save" @{
        firstName = $t.firstName
        lastName  = $t.lastName
        email     = $t.email
        studio    = @{ id = (StudioId $t.studio) }
        subjects  = $subjectRefs
    } | Out-Null
}
$tutors = @(Send Get "tutors/all")
function TutorId($email) { ($tutors | Where-Object { $_.email -eq $email }).id }

# ---------- Learners (5) - two share the school FAWE ----------
$learnerData = @(
    @{ firstName = "Ange";    lastName = "Ingabire"; learnerCode = "LRN-0098"; school = "FAWE";                 email = "ange@mail.rw" },
    @{ firstName = "Eric";    lastName = "Habimana"; learnerCode = "LRN-0099"; school = "FAWE";                 email = "eric@mail.rw" },
    @{ firstName = "Diane";   lastName = "Uwimana";  learnerCode = "LRN-0100"; school = "Green Hills Academy";  email = "diane@mail.rw" },
    @{ firstName = "Patrick"; lastName = "Mugisha";  learnerCode = "LRN-0101"; school = "Lycee de Kigali";      email = "patrick@mail.rw" },
    @{ firstName = "Sandra";  lastName = "Kamikazi"; learnerCode = "LRN-0102"; school = "Riviera High School";  email = "sandra@mail.rw" }
)
foreach ($l in $learnerData) { Send Post "learners/save" $l | Out-Null }
$learners = @(Send Get "learners/all")
function LearnerId($code) { ($learners | Where-Object { $_.learnerCode -eq $code }).id }

# ---------- Lessons (10) ----------
# D1 refuses past dates, so dates are counted from TODAY (they fall in Oct/Nov).
# The lesson always starts REQUESTED; we then walk it along the allowed arrows with D2.
function NewLesson($learnerCode, $tutorEmail, $dayOffset, $topic, $minutes, $moves) {
    $date = (Get-Date).AddDays($dayOffset).ToString("yyyy-MM-dd")
    Send Post "lessons/save" @{
        lessonDate      = $date
        topic           = $topic
        durationMinutes = $minutes
        learner         = @{ id = (LearnerId $learnerCode) }
        tutor           = @{ id = (TutorId $tutorEmail) }
    } | Out-Null
    $created = (@(Send Get "lessons/all") | Where-Object { $_.topic -eq $topic })
    foreach ($m in $moves) {
        Send Patch "lessons/$($created.id)/status?status=$m" $null | Out-Null
    }
    return @{ id = $created.id; date = $date }
}

# Tutor Alice has TWO lessons on the same day: first one DECLINED, then a second one is allowed.
$L1  = NewLesson "LRN-0098" "alice@edutech.rw" 1  "Quadratic equations"      60 @("DECLINED")
$L2  = NewLesson "LRN-0099" "alice@edutech.rw" 1  "Linear equations"         45 @()
$L3  = NewLesson "LRN-0098" "jean@edutech.rw"  2  "Essay writing"            60 @("ACCEPTED", "TAUGHT")
$L4  = NewLesson "LRN-0098" "jean@edutech.rw"  3  "Grammar basics"           45 @("ACCEPTED", "TAUGHT")
$L5  = NewLesson "LRN-0100" "grace@edutech.rw" 4  "Newton's laws"            60 @("ACCEPTED")
$L6  = NewLesson "LRN-0101" "alice@edutech.rw" 7  "Algebra revision"         90 @()
$L7  = NewLesson "LRN-0102" "jean@edutech.rw"  8  "Reading comprehension"    45 @()
$L8  = NewLesson "LRN-0099" "grace@edutech.rw" 10 "Optics"                   60 @("DECLINED")
$L9  = NewLesson "LRN-0100" "alice@edutech.rw" 15 "Geometry"                 60 @()
$L10 = NewLesson "LRN-0101" "grace@edutech.rw" 17 "Motion and forces"        75 @("ACCEPTED")

# ---------- Postman environment (ids used by the collection) ----------
$vars = [ordered]@{
    baseUrl            = "http://localhost:8080"
    learnerCode        = "LRN-0098"
    learnerId          = (LearnerId "LRN-0098")
    tutorId            = (TutorId "alice@edutech.rw")
    tutor3Id           = (TutorId "grace@edutech.rw")
    csSubjectId        = (SubjectId "Computer Science")
    lessonRequestedId  = $L2.id
    lessonAcceptedId   = $L5.id
    lessonTaughtId     = $L3.id
    lessonDeclinedId   = $L1.id
    doubleDate         = $L1.date
    futureDate         = (Get-Date).AddDays(40).ToString("yyyy-MM-dd")
    assessmentId       = ""
}
$values = @()
foreach ($k in $vars.Keys) { $values += @{ key = $k; value = "$($vars[$k])"; type = "default"; enabled = $true } }
$envObj = @{ id = "edutech-local-env"; name = "EduTech Local"; values = $values; "_postman_variable_scope" = "environment" }
ConvertTo-Json -InputObject $envObj -Depth 5 | Set-Content -Path ".\edutech.postman_environment.json" -Encoding UTF8

Write-Host ""
Write-Host "Seed finished. Quick check:" -ForegroundColor Green
Write-Host "  Learners : $((@(Send Get 'learners/all')).Count)"
Write-Host "  Tutors   : $((@(Send Get 'tutors/all')).Count)"
Write-Host "  Lessons  : $((@(Send Get 'lessons/all')).Count)"
Write-Host "Postman environment written to edutech.postman_environment.json"
