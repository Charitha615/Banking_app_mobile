<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Facades\Crypt;

class PayBill extends Model
{
    use HasFactory;

    protected $fillable = [
        'user_id', 'bill_type', 'account_number', 'amount', 'date', 'time'
    ];

    // Encrypt data before saving
    public function setAccountNumberAttribute($value)
    {
        $this->attributes['account_number'] = Crypt::encryptString($value);
    }

    // Decrypt data when retrieving
    public function getAccountNumberAttribute($value)
    {
        return Crypt::decryptString($value);
    }
}
