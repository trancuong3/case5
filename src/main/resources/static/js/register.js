function checkPasswordMatch() {
    const password = document.getElementById('password').value;
    const repass = document.getElementById('confirm_password').value;
    if (password !== repass) {
        document.getElementById('confirm_password').style.borderColor = 'red';
        document.getElementById('confirm_password').nextElementSibling.style.display = 'block';
        document.getElementById('confirm_password').nextElementSibling.textContent = 'Mật khẩu không khớp!';
        return false;
    }
    document.getElementById('confirm_password').style.borderColor = '';
    document.getElementById('confirm_password').nextElementSibling.style.display = 'none';
    return true;
}

document.getElementById('confirm_password').addEventListener('input', function() {
    checkPasswordMatch();
});