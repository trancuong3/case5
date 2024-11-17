document.getElementById('logoutForm').addEventListener('submit', function(e) {
    e.preventDefault();
    if (confirm('Bạn có chắc muốn đăng xuất?')) {
        this.submit();
    }
});