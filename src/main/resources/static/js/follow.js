document.addEventListener('DOMContentLoaded', function () {
    const followBtn = document.getElementById('follow-btn');
    // const blogUserId = /* Thymeleaf에서 제공한 blogUser.id 값을 가져오는 로직 */;
    // const currentUserId = /* Thymeleaf에서 제공한 현재 사용자의 ID를 가져오는 로직 */;

    async function toggleFollow() {
        const isFollowing = await fetch(`/api/follow/status?userId=${blogUserId}&followerId=${currentUserId}`)
            .then(response => response.json());

        if (isFollowing) {
            await fetch(`/api/follow/${blogUserId}?followerId=${currentUserId}`, { method: 'DELETE' });
            followBtn.textContent = '팔로우';
        } else {
            await fetch(`/api/follow/${blogUserId}?followerId=${currentUserId}`, { method: 'POST' });
            followBtn.textContent = '팔로우 취소';
        }
    }

    followBtn.addEventListener('click', toggleFollow);
});
