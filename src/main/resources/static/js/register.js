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

document.addEventListener("DOMContentLoaded", function() {
    const ownerBtn = document.getElementById("ownerBtn");
    const customerBtn = document.getElementById("customerBtn");
    const isOwnerInput = document.getElementById("isOwner");

    // Khi nhấp vào nút "Làm chủ nhà"
    ownerBtn.addEventListener("click", function() {
        ownerBtn.classList.add("selected");
        customerBtn.classList.remove("selected");
        isOwnerInput.value = ownerBtn.getAttribute("data-value"); // Cập nhật giá trị là true
    });

    // Khi nhấp vào nút "Làm khách hàng"
    customerBtn.addEventListener("click", function() {
        customerBtn.classList.add("selected");
        ownerBtn.classList.remove("selected");
        isOwnerInput.value = customerBtn.getAttribute("data-value"); // Cập nhật giá trị là false
    });
});
