function toggleLike(postId, isLiked) {
    console.log(postId, isLiked);

    let endpoint = isLiked ? `/remove/like?postId=${postId}` : `/add/like?postId=${postId}`;
    let method = isLiked ? 'DELETE' : 'POST';

    fetch(endpoint, { method: method })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json(); // JSON 데이터를 반환
        })
        .then(data => {
            console.log('Response data:', data);

            // 성공적으로 요청을 처리한 후 UI를 업데이트
            let likeBtn = document.getElementById('likeBtn');
            let unLikeBtn = document.getElementById('unLikeBtn');
            let likeCountSpan = document.getElementById('post-like-count');

            if (data.liked) {
                if (likeBtn) {
                    likeBtn.style.display = 'none';
                }
                if (unLikeBtn) {
                    unLikeBtn.style.display = 'inline';
                }
            } else {
                if (likeBtn) {
                    likeBtn.style.display = 'inline';
                }
                if (unLikeBtn) {
                    unLikeBtn.style.display = 'none';
                }
            }
            likeCountSpan.textContent = data.likeCount; // 좋아요 개수를 업데이트
        })
        .catch(error => {
            console.error('Error toggling like:', error);
        });
}
