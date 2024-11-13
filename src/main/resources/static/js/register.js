function checkPasswordMatch() {
    const password = document.getElementById('password').value;
    const repass = document.getElementById('passwordConfirm').value;
    if (password !== repass) {
        document.getElementById('passwordConfirm').style.borderColor = 'red';
        document.getElementById('passwordConfirm').nextElementSibling.style.display = 'block';
        return false;
    }
    document.getElementById('passwordConfirm').style.borderColor = '';
    document.getElementById('passwordConfirm').nextElementSibling.style.display = 'none';
    return true;
}

document.getElementById('passwordConfirm').addEventListener('input', function() {
    checkPasswordMatch();
});
