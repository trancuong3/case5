function checkPasswordMatch() {
    const password = document.getElementById('password').value;
    const repass = document.getElementById('confirmPassword').value;
    if (password !== repass) {
        document.getElementById('confirmPassword').style.borderColor = 'red';
        document.getElementById('confirmPassword').nextElementSibling.style.display = 'block';
        return false;
    }
    document.getElementById('confirmPassword').style.borderColor = '';
    document.getElementById('confirmPassword').nextElementSibling.style.display = 'none';
    return true;
}

document.getElementById('confirmPassword').addEventListener('input', function() {
    checkPasswordMatch();
});
