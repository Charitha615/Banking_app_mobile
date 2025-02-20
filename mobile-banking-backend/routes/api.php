<?php

use App\Http\Controllers\AuthController;
use App\Http\Controllers\NotificationController;
use App\Http\Controllers\PayBillController;
use App\Http\Controllers\TranferController;
use App\Models\Tranfer;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "api" middleware group. Make something great!
|
*/

Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
});

Route::post('register', [AuthController::class, 'register']);
Route::post('login', [AuthController::class, 'login']);
Route::post('logout', [AuthController::class, 'logout'])->middleware('auth:sanctum');

Route::middleware('auth:sanctum')->post('/users', [AuthController::class, 'getAllUsers']);
Route::get('/users/{id}', [AuthController::class, 'getUserById']);
Route::get('/users/{id}', [AuthController::class, 'getUserById']);
Route::post('/users/{id}/deactivate', [AuthController::class, 'deleteUserById']);
Route::post('/users/{id}/update', [AuthController::class, 'updateUser']);
Route::post('/users/{id}/update/balance', [AuthController::class, 'updateBalance']);

Route::middleware('auth:sanctum')->post('/paybills/create', [PayBillController::class, 'store']);
Route::post('/paybills/user/{userId}', [PayBillController::class, 'index']);
Route::middleware('auth:sanctum')->post('/paybills/{id}', [PayBillController::class, 'show']);
Route::get('/users/{user_id}/notifications', [NotificationController::class, 'getNotificationsByUserId']);

Route::post('/transfer/create', [TranferController::class, 'createTransfer']);

