function tokenSave(token) {
    window.localStorage.setItem("accessToken", token);
}

function tokenLoad() {
    return window.localStorage.getItem("accessToken");
}

function tokenRemove(){
    localStorage.removeItem("accessToken");
}