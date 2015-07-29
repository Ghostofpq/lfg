angular.module('lfg.controllers', [])

.controller('AppCtrl', function ($scope, $ionicModal, $timeout, $lfgRest, $state) {
    console.log("AppCtrl");
    if (!$lfgRest.isLoggedIn()) {
        $state.go('login');
    }
    $scope.logOut = function () {
        $lfgRest.logOut();
        $state.go('login');
    };

})

.controller('LoginCtrl', function ($scope, $ionicModal, $timeout, $lfgRest, $state) {
    console.log("LoginCtrl");

    if ($lfgRest.hasToken()) {
        console.log("context has token");
        $lfgRest.getUser()
            .success(function (res) {
                $lfgRest.setUserLocal(res);
                $state.go('app.playlists');
            })
            .error(function (err) {
                $scope.error = err.status + " : " + err.error;
            });
    }

    console.log("context has no token");
    $scope.loginData = {};
    $scope.doLogin = function () {
        console.log('Doing login', $scope.loginData);
        var login = $scope.loginData.login;
        var pass = $scope.loginData.password;

        $scope.loginData.password = "";

        $lfgRest.createToken(login, pass)
            .success(function (res) {
                console.log(res);
                $lfgRest.setToken(res);
                $lfgRest.getUser()
                    .success(function (getUserResponse) {
                        $lfgRest.setUserLocal(res);
                        $state.go('app.search');
                    })
                    .error(function (getUserError) {
                        console.log(getUserError);
                        $scope.error = err.message;
                    });
            })
            .error(function (err) {
                console.log(err);
                $scope.error = err.message;
            });
    };
    $scope.goToSignUp = function () {
        $state.go('signup');
    };
})

.controller('SignUpCtrl', function ($scope, $ionicModal, $timeout, $lfgRest, $state) {
        console.log("SignUpCtrl");
        $scope.signUpData = {};
        $scope.doSignUp = function () {
            console.log('Doing SignUp', $scope.signUpData);
            if (!($scope.signUpData.password === $scope.signUpData.repeatPassword)) {
                $scope.signUpData.password = "";
                $scope.signUpData.repeatPassword = "";
                $scope.error = "Password and Repeat Password should be the same";
            } else {
                var login = $scope.signUpData.login;
                var pass = $scope.signUpData.password;
                var email = $scope.signUpData.email;
                $scope.signUpData.password = "";
                $scope.signUpData.repeatPassword = "";
                $lfgRest.registerUser(login, pass, email)
                    .success(function (registerUserResponse) {
                        $lfgRest.setUserLocal(registerUserResponse);
                        $lfgRest.createToken(login, pass)
                            .success(function (createTokenResponse) {
                                console.log(createTokenResponse);
                                $lfgRest.setToken(createTokenResponse);
                                $state.go('app.search');
                            })
                            .error(function (err) {
                                console.log(err);
                                $scope.error = err.message;
                            });
                    })
                    .error(function (registerUserErr) {
                        console.log(registerUserErr);
                        $scope.error = registerUserErr.message;
                    });
            }
        };
        $scope.goToLogin = function () {
            $state.go('login');
        };
    })
    .controller('MapCtrl', function ($scope) {
        console.log("MapCtrl");
        $scope.map = {
            center: {
                latitude: 45,
                longitude: -73
            },
            zoom: 8
        };
    });
