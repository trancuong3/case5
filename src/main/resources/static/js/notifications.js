document.addEventListener('DOMContentLoaded', function () {
    const notificationList = document.getElementById('notificationList');

    // Gọi API để lấy thông báo
    fetch('/api/notifications')
        .then(response => {
            if (!response.ok) throw new Error('Network response was not ok');
            return response.json();
        })
        .then(data => {
            // Xóa nội dung cũ
            notificationList.innerHTML = '';

            // Render danh sách thông báo
            if (data.length === 0) {
                notificationList.innerHTML = '<p>Không có thông báo mới</p>';
            } else {
                data.forEach(notification => {
                    const item = document.createElement('a');
                    item.className = 'dropdown-item';
                    item.href = '#';
                    item.innerHTML = `<i class="fas fa-envelope mr-2"></i>
                        ${notification.userName} đã đặt thuê "${notification.houseName}" vào ngày ${new Date(notification.startDate).toLocaleDateString()}`;
                    notificationList.appendChild(item);
                });
            }
        })
        .catch(error => {
            console.error('Error fetching notifications:', error);
            notificationList.innerHTML = '<p>Không thể tải thông báo</p>';
        });
});