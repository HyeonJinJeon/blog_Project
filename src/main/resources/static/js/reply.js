// reply.js
function toggleReplies(button) {
    var repliesSection = button.nextElementSibling;
    if (repliesSection.style.display === 'none' || repliesSection.style.display === '') {
        repliesSection.style.display = 'block';
        button.textContent = '대댓글 숨기기';
    } else {
        repliesSection.style.display = 'none';
        button.textContent = '대댓글 보기';
    }
}
