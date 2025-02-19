<?php

namespace App\Http\Controllers;

use App\Models\PayBill;
use App\Models\User;
use Illuminate\Http\Request;

class PayBillController extends Controller
{
    // Create a new PayBill
    public function store(Request $request)
    {
        // Validate the request data
        $validatedData = $request->validate([
            'user_id' => 'required|exists:users,id',
            'bill_type' => 'required|string',
            'account_number' => 'required|string',
            'amount' => 'required|numeric',
            'date' => 'required|date',
            'time' => 'required|date_format:H:i:s',
        ]);

        // Retrieve the user
        $userId = $request->input('user_id');
        $user = User::find($userId);

        // Check if the user exists
        if (!$user) {
            return response()->json(['error' => 'User not found'], 404);
        }

        // Check if the user has sufficient balance
        $amount = $request->input('amount');
        if ($user->balance < $amount) {
            return response()->json(['error' => 'Insufficient balance'], 400);
        }

        // Deduct the amount from the user's balance
        $user->balance -= $amount;
        $user->save();

        // Create the PayBill record
        $payBill = PayBill::create($validatedData);

        // Return the created PayBill record with a 201 status code
        return response()->json($payBill, 201);
    }

    // Get all PayBills by user_id
    public function index($userId)
    {
        $payBills = PayBill::where('user_id', $userId)->get();

        $formattedBills = [
            'transactions' => $payBills->map(function ($bill) {
                return [
                    'bill_type' => $bill->bill_type,
                    'account_number' => $bill->account_number,
                    'amount' => '$' . number_format($bill->amount, 2),
                    'date' => $bill->created_at->toDateString(),
                    'time' => $bill->created_at->toTimeString(),
                ];
            })
        ];

        return response()->json($formattedBills);
    }


    // Get a single PayBill by id
    public function show($id)
    {
        $payBill = PayBill::findOrFail($id);
        return response()->json($payBill);
    }
}
